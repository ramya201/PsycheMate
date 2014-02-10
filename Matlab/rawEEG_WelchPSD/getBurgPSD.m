% Get Burg Power Spectral Density estimates for a given thought sample

function [Burg_PSD] = getBurgPSD(cell,j)

X1 = cell{1,:,j};
pxx1 = pburg(X1,512);
Burg_PSD = transpose(pxx1);

end


