filename_base = '../../EEGSamples/';
filename_thought = {'ThoughtB' 'ThoughtE' 'ThoughtF'};

filename_parameters = {'RawEEG.txt'};
delimiter = '/';
filename_str = 'Unassigned';

ThoughtB = cell(1,15);
ThoughtE = cell(1,15);
ThoughtF = cell(1,15);

l = 1;
for i=1:3
    for j=1:40            
        filename_str = strcat(filename_base,filename_thought(i),int2str(j),delimiter,filename_parameters(1));
        M = csvread(filename_str{:});
        M = M(1:5120);

        if (l==1)
            ThoughtB(:,j) = {transpose(M)};
        elseif (l==2)
            ThoughtE(:,j) = {transpose(M)};
        elseif (l==3)
            ThoughtF(:,j) = {transpose(M)};
        end

    end
    l = l+1;
end