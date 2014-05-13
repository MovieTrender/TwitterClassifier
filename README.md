## Read [Project Documentation](https://github.com/MovieTrender/Documentation "Project Documentation")

##TwitterClassifier

Classifier for tweets using a Naive Bayes model trained in Mahout.

This layer is outside of the scope of the project and will be included in the next version.

###Use

	hadoop jar TwitterClassifier.jar 
    <<Your Naive Bayes model folder>>
    <<Your Train set vectors document frequency count>> 
    <<Your Train set vectors dictionnary>>
    <<Sequence File with tweets to classify>>
    <<Output folder>>
    

###What it does?

Reads the Sequence file containing all the tweets and runs a MapReduce in which for each line takes the TweetID and the tweet related and classifies it using:

- Naive Bayes model
- Document Frequency
- Dicctionary

###References

Parts of the code used in this process were taken from [Chimpler](https://github.com/fredang/mahout-naive-bayes-example2 "Chimpler") and adapted for MovieTrender project.




	



