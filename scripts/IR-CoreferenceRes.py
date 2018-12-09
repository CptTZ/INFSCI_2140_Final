import en_coref_md

nlp = en_coref_md.load()
import pandas as pd

fin = pd.read_csv("pgh_review.csv")
rReviews = []
for i in range(fin.shape[0]):
    aReview = fin["comment_text"][i]
    rReview = nlp(aReview)._.coref_resolved
    rReviews.append(rReview)
fout = pd.concat([fin, pd.DataFrame({"comment_full": rReviews})], axis=1)
fout.to_csv("pgh_review_nlp.csv")
