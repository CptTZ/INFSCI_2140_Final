
# coding: utf-8

# In[1]:

# load packages
import pandas as pd
# import re
from nltk.sentiment.vader import SentimentIntensityAnalyzer
from nltk import tokenize


# In[2]:

# get search term
searchTerm = "chicken wings"
# get all the related reviews
relatedReviews = pd.read_csv('pgh_review.csv')


# In[3]:

relatedReviews.head()


# In[4]:

relatedReviews.shape


# In[5]:

# define a function to split a review into several sentences by the list of seperators
def getSplit(txt, seps):
    default_sep = seps[0]

    # we skip seps[0] because that's the default seperator
    for sep in seps[1:]:
        txt = txt.replace(sep, default_sep)
    return [i.strip() for i in txt.split(default_sep)]


# In[6]:

# define a function to check if search terms in the sentence
def check(main, sub_split):
    ind = -1
    for word in sub_split:
        ind = main.find(word, ind+1)
        if ind == -1:
            return False
    return True


# In[7]:

# define a function to get sentiment score of a sentence
def getSentiScore(sent):
    scores = dict([('pos', 0), ('neu', 0), ('neg', 0), ('compound', 0)])

    if not sent:
        return scores

    raw_text = sent
    # raw_text = re.sub("\n", ". ", str(raw_text))

    # Using already trained
    sid = SentimentIntensityAnalyzer()
    sentences = tokenize.sent_tokenize(raw_text)

    scores = dict([('pos', 0), ('neu', 0), ('neg', 0), ('compound', 0)])
    for sentence in sentences:

        ss = sid.polarity_scores(sentence)

        for k in sorted(ss):
            scores[k] += ss[k]

    return scores['compound']


# In[8]:

# new a dictionary to save the bid and sentiScore
bidScoreDict = {}
# initialize flag
k = 0
flag = 0
# the list of seperators
cutSymbol = [',','.','!','?',';','and','or','but','however','yet','though','although','even','while']
# cut each review into several sentences
for i in range(relatedReviews.shape[0]):
    k = k + 1
    # initialize relatedSents
    relatedSents = ""
    # get the business id of the ith review
    bid = relatedReviews['business_id'][i]
    # get the ith review and make all characters lowercased
    aReview = relatedReviews['comment_full'][i].lower()
    # split the review by cutSymbol
    sentsReview = getSplit(aReview, cutSymbol)
    for sent in sentsReview:
        if check(sent, searchTerm.lower().split(' ')):
            relatedSents = relatedSents + '. ' + sent
            flag = 1
    if flag == 1:
        print(relatedSents)
        # get sentiment score of relatedSents
        sentiScore = getSentiScore(relatedSents)
        print(sentiScore)
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
    if k >= 2000:
        print(bidScoreDict)
        break


# In[9]:

# calculate the popular score of each restaurant
bidRatioDict={}
for bid in bidScoreDict:
    bidRatioDict[bid] = bidScoreDict[bid][0] / bidScoreDict[bid][1]


# In[10]:

bidRatioDict


# In[ ]:



