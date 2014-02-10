% Statistical feature extraction of all thought samples in a thought category
function [Stat_Features] = getStatFeatures(cell,j)

X1 = cell{1,:,j};
p1 = [mean(X1) median(X1) std(X1) var(X1)];
Stat_Features = p1;

end


