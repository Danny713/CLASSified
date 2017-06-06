import json
import sys

with open('courses_1182.json', 'r') as myfile: # change the .json file name accordingly
	json_data_string = myfile.read().replace('\n', '')
ds = json.loads(json_data_string) #this contains the json
unique_stuff = { each['courseid'] : each for each in ds }.values()
first = True
for course in unique_stuff:
	if first:
		first = False
		print '['
	else:
		print ','
	json.dump(course, sys.stdout)
print ']'