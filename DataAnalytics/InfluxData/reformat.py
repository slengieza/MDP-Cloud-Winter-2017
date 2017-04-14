f = open("data1.txt", "r")

for line in f:
    line = line.strip().split(",")
    print(line[2][1:] + "\t" + line[3][1:len(line[3])-1])
