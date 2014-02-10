% Function to read all raw EEG csv files of each of the 3 thoughts and form
% a cell structure of size 1 x 5120 x 40.
% Recording time: 10ms, No of samples per thought: 40

filename_base = '../../EEGSamples/';
filename_thought = { 'ThoughtB' 'ThoughtE' 'ThoughtF'};
filename_parameters = {'RawEEG.txt'};
delimiter = '/';
filename_str = 'Unassigned';


ThoughtB = cell(1,5120,40);
ThoughtE = cell(1,5120,40);
ThoughtF = cell(1,5120,40);

l = 1;
for i=1:3
    for j=1:40
            filename_str = strcat(filename_base,filename_thought(i),int2str(j),delimiter,filename_parameters(1));
            M = csvread(filename_str{:});
            
            if (l==1)
                ThoughtB(1,:,j) = {transpose(M(1:5120,:))};
            elseif (l==2)
                ThoughtE(1,:,j) = {transpose(M(1:5120,:))};
            elseif (l==3)
                ThoughtF(1,:,j) = {transpose(M(1:5120,:))};
            end
    end
    l = l+1;
end
