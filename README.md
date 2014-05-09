##TwitterClassifier

Classifier for tweets using a Naive Bayes model trained in Mahout

###Use

	hadoop jar TwitterClassifier.jar 
    <<Your Naive Bayes model folder>>
    <<Your Train set vectors document frequency count>> 
    <<Your Train set vectors dictionnary>>
    <<Sequence File with tweets to classify>>
    <<Output folder>>
    

###What it does?

Reads the Sequence file with all the tweets and run a MapReduce in which for each line take the TweetID and the tweet related and classifies it using:

- Naive Bayes model
- Document Frequency
- Dicctionary

###References

Parts of the code used in this process were taken from [Chimpler](https://github.com/fredang/mahout-naive-bayes-example2 "Chimpler") and addapted for MovieTrender project.




	



