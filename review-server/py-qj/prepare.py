import sys
def main():
	path = '/home/work/data/opinion_spam/real/10lines'
	opath = '/home/work/data/opinion_spam/real/output'
	output_file = open(opath, 'w')
	raw_file = open(path)
	line = raw_file.readline()
	while line:
		item = line.split("\t")
		if len(item) == 8:
			hfd = float(item[3])
			fd = float(item[4])
			ratings = float(item[5])
			body = item[7]
			if fd > 20:
				hfdfd = hfd/fd
				string = str(hfdfd)+" "+ body+"\n"
				output_file.write(string)
		line = raw_file.readline()
	output_file.close()
	raw_file.close()
				
if __name__ == '__main__':
	main()

