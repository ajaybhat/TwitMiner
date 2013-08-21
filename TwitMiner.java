/*
 * TwitMiner 2013 Program
 * Created by Ajay Bhat of team : Beta Bots
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Scanner;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class TwitMiner {
	private Instances trainingData;
	private StringToWordVector filter;
	private Classifier classifier;
	private boolean upToDate;
	private FastVector classValues;
	private FastVector attributes;
	private boolean setup;
	private BufferedReader input;
	private Instances filteredData;

	public static void main(String[] args) throws FileNotFoundException {
		new TwitMiner().getOutput();
	}

	public TwitMiner() throws FileNotFoundException {
		input = new BufferedReader(new InputStreamReader(new FileInputStream(
				"Training.txt")));
		/*
		 * Input is read from file Training.txt and the operations performed on
		 * file Test.txt. The output is written to file Output.txt
		 */
	}

	public TwitMiner(Classifier classifier) throws FileNotFoundException {
		this(classifier, 10);// classifier set with capacity 10
	}

	public TwitMiner(Classifier classifier, int startSize)
			throws FileNotFoundException {
		this.filter = new StringToWordVector();
		this.classifier = classifier;
		// Create vector of attributes.
		this.attributes = new FastVector(2);
		// Add attribute for holding texts.
		this.attributes.addElement(new Attribute("text", (FastVector) null));
		// Add class attribute.
		this.classValues = new FastVector(startSize);
		this.setup = false;

	}

	private void getOutput() throws FileNotFoundException
	// function for getting the output file
	{
		String label, tweettext, line, output, hashtag = "";
		System.setOut(new PrintStream("Output.txt"));
		Scanner s;
		double result[], hres[];// result arrays for storing the result of
								// classifier and hashtag classifier
		try {
			// create text classifier and hashtag classifier
			TwitMiner classifier = new TwitMiner(new NaiveBayesMultinomial()), hashclassifier = new TwitMiner(
					new NaiveBayesMultinomial());

			// created Naive Bayes Classifier from Weka
			// add the category Politics and Sports to each classifier
			classifier.addCategory("Politics");
			classifier.addCategory("Sports");
			classifier.setupAfterCategorysAdded();
			hashclassifier.addCategory("Politics");
			hashclassifier.addCategory("Sports");
			hashclassifier.setupAfterCategorysAdded();

			// get data from Training.txt
			while ((line = input.readLine()) != null) {
				s = new Scanner(line);

				// scan each line for text & category label
				tweettext = "";
				label = s.next();
				label = s.next();
				hashtag = "";

				while (s.hasNext()) {
					String temp = s.next();
					// check for the token being a hashtag, if its a hashtag
					// isolate it
					if (temp.charAt(0) == '#') {
						if (temp.endsWith("\"") || temp.endsWith("'")
								|| temp.endsWith(",") || temp.endsWith("."))
							temp = temp.substring(0, temp.length() - 1);
						hashtag = temp;
					}
					tweettext = tweettext.concat(temp + " ");
				}

				// trim the text for trailing punctuation marks
				StringBuffer temp = new StringBuffer(tweettext);
				temp.setCharAt(0, ' ');
				temp.setCharAt(temp.length() - 1, ' ');
				temp.setCharAt(temp.length() - 2, ' ');
				tweettext = temp.toString();
				tweettext = tweettext.trim();

				// added text and hashtag if any to respective classifier
				classifier.addData(tweettext, label);
				hashclassifier.addData(hashtag, label);
			}
			input = new BufferedReader(new InputStreamReader(
					new FileInputStream("Test.txt")));
			// switched input to Test.txt
			while ((line = input.readLine()) != null) {

				s = new Scanner(line);
				// get tweet text and tweet id in variables tweettext and label
				// respectively
				hashtag = "";
				tweettext = "";
				label = s.next();
				while (s.hasNext()) {
					String temp = s.next();
					if (temp.charAt(0) == '#') {
						if (temp.endsWith("\"") || temp.endsWith("'")
								|| temp.endsWith(",") || temp.endsWith("."))
							temp = temp.substring(0, temp.length() - 1);
						hashtag = temp;
					}
					tweettext = tweettext.concat(temp + " ");
				}
				// do as for Training tweettext
				StringBuffer temp = new StringBuffer(tweettext);
				temp.setCharAt(0, ' ');
				temp.setCharAt(temp.length() - 1, ' ');
				temp.setCharAt(temp.length() - 2, ' ');
				tweettext = temp.toString();
				tweettext = tweettext.trim();

				// perform the prediction algorithm and store it in the result
				// arrays
				result = classifier.classifyMessage(tweettext);
				hres = hashclassifier.classifyMessage(hashtag);

				// first check for the probability of the result
				if (result[0] > 0.8)
					output = "Politics";
				else if (result[1] > 0.8)
					output = "Sports";
				else {

					// if probability not clear the hashtag is checked
					if (hashtag != "") {
						if (hres[0] > 0.6)
							output = "Politics";
						else if (hres[1] > 0.6)
							output = "Sports";
						// combination of result and hashtags if any are checked
						else if (result[0] > 0.5 && hres[0] > 0.5)
							output = "Politics";
						else if (result[1] > 0.5 && hres[1] > 0.5)
							output = "Sports";
						else 
							output = "Sports";// set default label as Sports
					} else
						/*
						 * if it cannot be identified even after checking the
						 * classifier, give the label as Sports. This method
						 * would have a 50% accuracy rate.
						 */
						output = "Sports";// set default label as Sports
					}
				// print out the tweetid and corresponding label
				System.out.println(label + "\t" + output);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void addCategory(String category) {
		category = category.toLowerCase();
		// if required, double the capacity.
		int capacity = classValues.capacity();
		if (classValues.size() > (capacity - 5)) {
			classValues.setCapacity(capacity * 2);// if we nearing the capacity,
													// doubled the capacity
		}
		classValues.addElement(category);
	}

	public void addData(String message, String classValue)
			throws IllegalStateException {
		if (!setup) {
			throw new IllegalStateException("Must use setup first");
		}
		message = message.toLowerCase();
		classValue = classValue.toLowerCase();
		// Make message into instance.
		Instance instance = makeInstance(message, trainingData);
		// Set class value for instance.
		instance.setClassValue(classValue);
		// Add instance to training data.
		trainingData.add(instance);
		upToDate = false;
	}

	/*
	 * Check whether classifier and filter are up to date. Build if necessary.
	 * 
	 * @throws Exception
	 */
	private void buildIfNeeded() throws Exception {
		if (!upToDate) {
			// Initialize filter and tell it about the input format.
			filter.setInputFormat(trainingData);
			// Generate word counts from the training data.
			filteredData = Filter.useFilter(trainingData, filter);
			// Rebuild classifier.
			classifier.buildClassifier(filteredData);
			upToDate = true;
		}
	}

	public double[] classifyMessage(String message) throws Exception
	// Prediction algorithm
	{
		message = message.toLowerCase();
		if (!setup) {
			throw new Exception("Must use setup first");
		}
		// Check whether classifier has been built.
		if (trainingData.numInstances() == 0) {
			throw new Exception("No classifier available.");
		}
		buildIfNeeded();
		Instances testset = trainingData.stringFreeStructure();
		Instance testInstance = makeInstance(message, testset);

		// Filter instance.
		filter.input(testInstance);
		Instance filteredInstance = filter.output();
		return classifier.distributionForInstance(filteredInstance);

	}

	private Instance makeInstance(String text, Instances data) {
		// Create instance of length two.
		Instance instance = new Instance(2);
		// Set value for message attribute
		Attribute messageAtt = data.attribute("text");
		instance.setValue(messageAtt, messageAtt.addStringValue(text));
		// Give instance access to attribute information from the dataset.
		instance.setDataset(data);
		return instance;
	}

	public void setupAfterCategorysAdded() {
		attributes.addElement(new Attribute("class", classValues));
		// Create dataset with initial capacity of 100, and set index of class.
		trainingData = new Instances("MessageClassificationProblem",
				attributes, 100);
		trainingData.setClassIndex(trainingData.numAttributes() - 1);
		setup = true;
	}

}
