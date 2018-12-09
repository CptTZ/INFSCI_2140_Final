import json
import numpy as np
import pandas as pd
import sys
from nltk import tokenize
from nltk.sentiment.vader import SentimentIntensityAnalyzer


# define a function to split a review into several sentences by the list of seperators
def getSplit(txt, seps):
    default_sep = seps[0]
    # we skip seps[0] because that's the default seperator
    for sep in seps[1:]:
        txt = txt.replace(sep, default_sep)
    return [i.strip() for i in txt.split(default_sep)]


# define a function to check if search terms in the sentence
def check(main, sub_split):
    ind = -1
    for word in sub_split:
        ind = main.find(word, ind + 1)
        if ind == -1:
            return False
    return True


# define a function to get sentiment score of a sentence
def getSentiScore(sent):
    scores = dict([("pos", 0), ("neu", 0), ("neg", 0), ("compound", 0)])

    if not sent:
        return scores

    raw_text = sent
    # Using already trained
    sid = SentimentIntensityAnalyzer()
    sentences = tokenize.sent_tokenize(raw_text)

    scores = dict([("pos", 0), ("neu", 0), ("neg", 0), ("compound", 0)])
    for sentence in sentences:
        ss = sid.polarity_scores(sentence)
        for k in sorted(ss):
            scores[k] += ss[k]
    return scores["compound"]


def ranking(searchTerm, path):
    # get all the related reviews
    relatedReviews = pd.read_csv(path)

    # new a dictionary to save the bid and sentiScore
    bidScoreDict = {}
    # initialize flag
    flag = 0
    # the list of seperators
    cutSymbol = [
        ",",
        ".",
        "!",
        "?",
        ";",
        "and",
        "or",
        "but",
        "however",
        "yet",
        "though",
        "although",
        "even",
        "while",
    ]
    # cut each review into several sentences
    for i in range(relatedReviews.shape[0]):
        # initialize relatedSents
        relatedSents = ""
        # get the business id of the ith review
        bid = relatedReviews["business_id"][i]
        # Some may be nan (Unknown)
        aReview = relatedReviews["comment_full"][i]
        if aReview is np.nan:
            aReview = relatedReviews["comment_text"][i]
        # get the ith review and make all characters lowercased
        aReview = aReview.lower()
        # split the review by cutSymbol
        sentsReview = getSplit(aReview, cutSymbol)
        for sent in sentsReview:
            if check(sent, searchTerm.lower().split(" ")):
                relatedSents = relatedSents + ". " + sent
                flag = 1
        if flag == 1:
            # get sentiment score of relatedSents
            sentiScore = getSentiScore(relatedSents)
            if bid in bidScoreDict:
                if sentiScore > 0:
                    bidScoreDict[bid][0] = bidScoreDict[bid][0] + 1
                    bidScoreDict[bid][1] = bidScoreDict[bid][1] + 1
                else:
                    bidScoreDict[bid][1] = bidScoreDict[bid][1] + 1
            else:
                if sentiScore > 0:
                    bidScoreDict[bid] = [1, 1]
                else:
                    bidScoreDict[bid] = [0, 1]
            flag = 0
    # calculate the score of each restaurant
    bidRatioDict = {}
    for bid in bidScoreDict:
        bidRatioDict[bid] = bidScoreDict[bid][0] / bidScoreDict[bid][1]
    print(json.dumps(bidRatioDict))


ranking(sys.argv[2], sys.argv[1])
