TwitMiner
=========

This is a program I created for TwitMiner 2013, a contest on data mining conducted in spring 2013 by IISc,Bangalore.
Given a set of random tweets gathered from Twitter that have been categorized into either 'Politics' or 'Sports' categories, it is required to use this data as training set for classification of a test set of tweets into the same categories, Politics or Sports.
Using proper classification algorithm it is possible to achieve high degree of accuracy in the classification of test data.

Tweets are identified using their Tweet IDs since it is not allowed to use the texts by looking at them manually for finding the categories.

Input:

Training data : <tweetid tweettext label>
Test data : <tweetid tweettext>

Output:

<tweetid label>

Requirements:

Windows XP/Vista/7/8 or Linux OS
Java SDK v5.0 or higher
Weka API jar (Download at http://www.java2s.com/Code/JarDownload/weka/weka.jar.zip)
IDE for compiling and running the Java program, preferably Eclipse for Java or NetBeans

Instructions:

1. Extract the contents of the .zip file.
2. The .java file TwitMiner.java contains the program. It can be opened as a regular .txt file.
	a) Create a new Project in Eclipse using File->New->Java Project. 
	b) In the Project src folder, click on src folder and then goto File->New->Class. 
	c) Use the TwitMiner.java file as the new class.
3. Add the Weka API to the libraries used by TwitMiner.java.
	a) To do this in Eclipse : Goto Project->Properties->Java Build Path->Libraries Tab. 
	b) Click Add External JARs. Navigate to the Weka.jar folder and add the Weka.jar.
	c) It should show up in the Build Path.
4. Use the Training.txt and Test.txt file with format described in the Documentation in the same project folder as TwitMiner.java file.
5. Compile the .java file.
6. Run the program using the main function defined in the class. There is no need to give any arguments to main function.
7. Output is written to the file Output.txt, which can be found in same project folder.
