function [tagName, tag, date] = recentTagInfo(tags, folderDir)
%Input: which tags to read and directory of files to read
%Output: tagNames, tag, and data timestamps of most RECENTLY MODIFIED file 
%       in directory in alphebetical order of tag names
%Note: If any parts are confusing, remove semicolon at the end of the line
%       to see output of that line, this will show steps

%converts tags to cell array
tagsCell = cell(1,1);
i = 1;

tempArray = [];
findSpaces = (tags == 36); %36 is ascii value of $
numTags = sum(findSpaces);
for n = 1:numTags
    tempArray = [];
    while (~(findSpaces(i)))
        tempArray = [tempArray tags(i)];
        i = i + 1;
    end
    i = i + 1;
    tagsCell{n} = tempArray;
end
tags = tagsCell;

%turns ascii values into strings
tags = cellfun(@char, tags, 'UniformOutput', false);
%tags = cellfunASCIIconvert(tags);
folderDir = char(folderDir);

cd (folderDir);%moves to directory
d = dir();%stores all files in directory in variable d

%sets d to only csv files in the directory
out = regexp({d.name},'\S+\.csv?\>','match');
d = d(~(cellfun(@isempty, out)));

[x,y] = sort({d.date}); %sorts d by date (last to recent)
d = d(y); %reorders d so it is sorted
r = d(end); %stores the most recent file in variable

file = xlsread(r.name); %reads in numerical data from file
[rows, columns] = size(file); 
%accounts for different amounts of tags in csv file
colLength = {'A1:A1', 'A1:B1', 'A1:C1', 'A1:D1', 'A1:E1', 'A1:F1',... 
    'A1:G1', 'A1:H1', 'A1:I1', 'A1:J1', 'A1:K1', 'A1:L1', 'A1:M1',...
    'A1:N1', 'A1:O1', 'A1:P1', 'A1:Q1', 'A1:R1', 'A1:S1', 'A1:T1',... 
    'A1:U1', 'A1:V1', 'A1:W1', 'A1:X1', 'A1:Y1', 'A1:Z1'};
[useless, tagNames] = xlsread(r.name, colLength{1, columns});%reads in tag names from top row

%for personal testing (returns to folder containing program)
%cd ('\\engin-labs.m.storage.umich.edu\aramache\windat.v2\Desktop\Matlab stuff');

%CREATES 1 ROW ARRAY THAT CORRESPONDS TO WANTED TAG NAME LOCATIONS IN FILE
%   makes logical array for each given tag name compared to top row
tagNameLogic = cellfun(@(y)cellfun(@(x)strcmp(x,y), tagNames), tags, 'UniformOutput', false);
%combines logical arrays for each tage name into a one row array with
%   length "column"
if length(tags) ~= 1
    tagNameLogic = sum((reshape(cell2mat(tagNameLogic), columns, []))');
else
    tagNameLogic = (reshape(cell2mat(tagNameLogic), columns, []))';
end

%stores tag names in order found in file
fileTagNames = tagNames(logical(tagNameLogic));

%makes matrix with each row as the logical array, and filler row of zeroes to represent tag names
%   #rows = #rows of "file" (number of tag values per tag)
tagLocations = zeros(1, columns);
for n = 1:rows
    tagLocations = [tagLocations; tagNameLogic];
end
logic = logical(tagLocations([2:end],:));

%pulls tag values based on logical matrix
tagVals = file(logic);


%Sets the order of outputs to desired format: all values of each tag
%   together and the tags in alphebetical order, uses fileTagNames from above
[sortedTags,alphIndeces] = sort(fileTagNames); %finds alphebetical order
tagVals = reshape(tagVals, rows, []); %puts each tag's values in separate rows
tagVals = tagVals(:,alphIndeces); %sorts these rows based on the alph order
tagVals = (reshape(tagVals, rows, []))'; %puts values back into a row (change
                                      %rows to 1 and remove ' to turn into 
                                      %array)


%stores values in outputs
tagName = tags;
tag = tagVals; %this can be a matrix with each tag its own row if needed
date = file([2:end], 1);
%possibly add output
%numValsPerTag = rows






end

