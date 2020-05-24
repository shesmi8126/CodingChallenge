Shelby Smith
05/24/2020
Coding Challenge

Summary of the prupose of this repo:

The purpose of this repo I felt like was to see if I had the ability to use libaries. Then in addition to using the libary I had to have the ability to 
extract information from a CSV file, organize that information, and then create a database, a log, and another CSV file with the information. 



Steps for getting this app running:

In order to get this application running please make sure to have a database file set up with table called VaildData and have columns A through J. 


Overview of approach, design choices, and assumptions:

At first I was thinking of having to make classes to call to main but then as I was learning more about how to use opencsv 
I realized it would be easier to just create functions to call to main. I started out with first being able to pull the information
from the CSV file and organizing it by "bad" and "good" data as well as count how many there are of each and test it in console and printing
out the results. Once I had that completed I was able to move that data into the .log file and remove the code that prints the information in console. 
Then creating a .CSV file to push the "bad" data to and then moved on to the most difficult part for me which was uploading the "good" data to 
the already created db with columns A through J. My assumption was that if information was to be uploaded to the database that a database would have already been created. 

My design choices was to make everything simple and to the point. It gathers the proper information and creates/ pushes the information into the desired files.