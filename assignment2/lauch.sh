hadoop jar yelp.jar Q1 /yelp/business /user/hxy162130/Q1
hadoop jar yelp.jar Q2 /yelp/business /user/hxy162130/Q2
hadoop jar yelp.jar Q3 /yelp/business /user/hxy162130/Q3
hadoop jar yelp.jar Q4 /yelp/review /user/hxy162130/Q4
hadoop jar yelp.jar Q5 /yelp/review /user/hxy162130/Q5

echo ‘answer for Q1’
hdfs dfs -cat Q1/part-r-00000
echo ‘answer for Q2’
hdfs dfs -cat Q2/part-r-00000
echo ‘answer for Q3’
hdfs dfs -cat Q3/part-r-00000
echo ‘answer for Q4’
hdfs dfs -cat Q4/part-r-00000
echo ‘answer for Q5’
hdfs dfs -cat Q5/part-r-00000
