#!/bin/bash
cd src/com/wordcount/
javac WordCount.java
cd ../../../bin/
java com.wordcount.WordCount ../wc_input ../wc_output
echo "Done!"
