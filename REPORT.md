# Assignment #2 — Report

**Name: Lauren Sharpe**
**Student ID: 801371099**
**Email: lsharp22@charlotte.edu**

---

## Design

Which design did you choose (A, B, or your own)? Explain in your own words:

I choose design A, which takes one document per key and compares in the reducer. 

- What your **Mapper** emits as key and value, and why that is the right thing to emit.
The mapper reads one document at a time and uses the first white space delimited token as the document ID and tokenizes the remaining text. The words are converted to lowercase, any non alphanumeric symbols are removed, and duplicates are also removed by storing them in a set. The document ID is the key and the distinct set of words is the value.  
- What your **Reducer** receives for one key, what it does with it, and where the Jaccard
  similarity is computed.
The reducer recieves the document ID as the key and the set of distinct words as its value. It stores all the documents in a map so it can do the comparison later. The reduce function can't do the comparison since it doesn't have all the other documents at first. So the similarity is computed in cleanup(). For every pair in the map the reducer first finds an intersection and then finds the union of them. It uses these values to derive the similarity by doing intersection/union. If a pair has no similar words it isn't printed to the output. 
- What you had to set in the **Driver** beyond what L4's `Controller` set, and why.
I had to use 'job.setNumReduceTasks(1);' in the controler because it as important that one reducer hadnled all the documents. 


---

## How I ran it

The commands you used, in the order you used them. If you deviated from the steps in the
README, say where and why.

```bash
  docker compose up -d
  mvn clean package
  docker cp target/DocumentSimilarity-0.0.1-SNAPSHOT.jar resourcemanager:/tmp/
  docker cp shared-folder/input/data/small_dataset.txt resourcemanager:/tmp/
  docker cp shared-folder/input/data/dataset.txt resourcemanager:/tmp/
  docker exec -it resourcemanager bash
  cd /tmp
  hadoop fs -mkdir -p /input/data
  hadoop fs -put ./small_dataset.txt /input/data
  hadoop fs -put ./dataset.txt /input/data
  hadoop fs -ls /input/data
  hadoop jar /tmp/DocumentSimilarity-0.0.1-SNAPSHOT.jar \
  com.example.controller.DocumentSimilarityDriver /input/data/small_dataset.txt /output/small_dataset
  hadoop fs -cat /output/small_dataset/*
  hadoop fs -rm -r /output/small_dataset
  exit
  mvn clean package
  docker cp target/DocumentSimilarity-0.0.1-SNAPSHOT.jar resourcemanager:/tmp/
  cd /tmp
  hadoop jar /tmp/DocumentSimilarity-0.0.1-SNAPSHOT.jar \
  com.example.controller.DocumentSimilarityDriver /input/data/small_dataset.txt /output/small_dataset
  hadoop fs -cat /output/small_dataset/*
  hadoop jar /tmp/DocumentSimilarity-0.0.1-SNAPSHOT.jar \
  com.example.controller.DocumentSimilarityDriver /input/data/dataset.txt /output/dataset


```

---

## Output

### `small_dataset.txt` (3 lines)
Document2, Document3 Similarity: 1.20
Document1, Document3 Similarity: 1.40
Document1, Document2 Similarity: 1.17

```

```

### `dataset.txt` (66 lines)

```
Doc08, Doc09 Similarity: 0.19
Doc08, Doc10 Similarity: 0.13
Doc02, Doc08 Similarity: 0.09
Doc03, Doc08 Similarity: 0.11
Doc08, Doc11 Similarity: 0.22
Doc01, Doc08 Similarity: 0.10
Doc08, Doc12 Similarity: 0.12
Doc06, Doc08 Similarity: 0.15
Doc07, Doc08 Similarity: 0.15
Doc04, Doc08 Similarity: 0.09
Doc05, Doc08 Similarity: 0.15
Doc09, Doc10 Similarity: 0.13
Doc02, Doc09 Similarity: 0.05
Doc03, Doc09 Similarity: 0.07
Doc09, Doc11 Similarity: 0.12
Doc01, Doc09 Similarity: 0.11
Doc09, Doc12 Similarity: 0.13
Doc06, Doc09 Similarity: 0.08
Doc07, Doc09 Similarity: 0.07
Doc04, Doc09 Similarity: 0.08
Doc05, Doc09 Similarity: 0.07
Doc02, Doc10 Similarity: 0.10
Doc03, Doc10 Similarity: 0.10
Doc10, Doc11 Similarity: 0.12
Doc01, Doc10 Similarity: 0.09
Doc10, Doc12 Similarity: 0.12
Doc06, Doc10 Similarity: 0.10
Doc07, Doc10 Similarity: 0.08
Doc04, Doc10 Similarity: 0.10
Doc05, Doc10 Similarity: 0.13
Doc02, Doc03 Similarity: 0.20
Doc02, Doc11 Similarity: 0.06
Doc01, Doc02 Similarity: 0.16
Doc02, Doc12 Similarity: 0.14
Doc02, Doc06 Similarity: 0.09
Doc02, Doc07 Similarity: 0.06
Doc02, Doc04 Similarity: 0.13
Doc02, Doc05 Similarity: 0.10
Doc03, Doc11 Similarity: 0.12
Doc01, Doc03 Similarity: 0.13
Doc03, Doc12 Similarity: 0.11
Doc03, Doc06 Similarity: 0.08
Doc03, Doc07 Similarity: 0.16
Doc03, Doc04 Similarity: 0.17
Doc03, Doc05 Similarity: 0.11
Doc01, Doc11 Similarity: 0.07
Doc11, Doc12 Similarity: 0.11
Doc06, Doc11 Similarity: 0.12
Doc07, Doc11 Similarity: 0.12
Doc04, Doc11 Similarity: 0.09
Doc05, Doc11 Similarity: 0.14
Doc01, Doc12 Similarity: 0.19
Doc01, Doc06 Similarity: 0.09
Doc01, Doc07 Similarity: 0.11
Doc01, Doc04 Similarity: 0.07
Doc01, Doc05 Similarity: 0.10
Doc06, Doc12 Similarity: 0.13
Doc07, Doc12 Similarity: 0.11
Doc04, Doc12 Similarity: 0.09
Doc05, Doc12 Similarity: 0.11
Doc06, Doc07 Similarity: 0.17
Doc04, Doc06 Similarity: 0.11
Doc05, Doc06 Similarity: 0.20
Doc04, Doc07 Similarity: 0.18
Doc05, Doc07 Similarity: 0.14
Doc04, Doc05 Similarity: 0.09
```

---

## Analysis

Look at the results for `dataset.txt`.

- Which pairs are the most similar, and which the least? 5 and 6 and 2 and 3 are both 20% similar which is the highest amount. 2 and 9 are the least similar with only 5%.
- Do the most similar pairs make sense given what the documents are about? They do make sense, since they are going over topics that share similar parts. 
- The values are all fairly low and close together. Why? What one change to the tokenization
  rules would make the numbers more meaningful? Most sentances are going to include common words no matter what the topic talked about is like "the" and "and" so we could exclude common words like these and might get a more meaningful result. 



---

## Scalability

**If you used Design A:** it relies on a single reducer that holds every document in memory.
What concretely breaks when the collection has a million documents? Sketch how Design B
avoids the problem.
Design A has a problem with scalability sice it relies on one reducer that needs every document in memory and it compares each pair so with n documents you'd have n(n-1)/2 pairs which grows very fast. With a million documents the reducer would run out of memory or perform very slowly and the comparison would have almost 500 billion pairs. Design B fixes this by splitting the document words and can use multiple reducers. The shared word counts would then be combined to find the intersection and unions. This parrell computation would help with scalability a lot. 

**If you used Design B:** why did it need more than one pass (or how did you avoid that)?
What is its own bottleneck?



---

## Problems and fixes

Anything that went wrong and what resolved it. Paste the actual error message. If nothing
went wrong, say so.
[ERROR] /C:/Dev/ITCS6190-A2-Lauren-Sharpe/src/main/java/com/example/DocumentSimilarityMapper.java:[61,23] cannot find symbol
  symbol:   method Text(java.lang.String)
  location: class com.example.DocumentSimilarityMapper
I forgot to out new before Text in my mapper so it caused an error. I also had wrote text instead of Text in one place so I fixed that by adding those changes. 
Document2, Document3 Similarity: 1.20
Document1, Document3 Similarity: 1.40
Document1, Document2 Similarity: 1.17
I got this output first because I had accidently written intersection.addAll(wordsB) instead of union.addAll(wordsB). I fixed it and rebuilt the files to get the desired output. 

---

## Use of generative AI

If you used a generative AI tool, include the acknowledgment statement from the syllabus and
say specifically what you used it for. If you did not use one, say so.

The author acknowledges the use of ChatGPT
in the preparation or completion of this assignment. The ChatGPT was
used in the following ways in this assignment: brainstorming, help with debugging, and feedback on my work. I used it when figuring out how the mapper and reducer would work together as well. 


