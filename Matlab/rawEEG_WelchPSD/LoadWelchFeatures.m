% Script to form a matrix of Welch features for all thought samples (120 in
% total) This matrix is used as input for neural networks.
% Same script can be used with different feature extraction functions.

for i = 1:40
    temp = getWelchPSD(ThoughtB,i);
    Welch(i,:) = temp;
end
for i = 1:40
    temp = getWelchPSD(ThoughtE,i);
    Welch(i+40,:) = temp;
end
for i = 1:40
    temp = getWelchPSD(ThoughtF,i);
    Welch(i+80,:) = temp;
end