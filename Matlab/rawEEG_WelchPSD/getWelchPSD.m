% Get Welch Power Spectral Density estimates for a given thought sample

function [Welch_PSD] = getWelchPSD(cell,j)

X1 = cell{1,:,j};
pxx1 = pwelch(X1,512);
Welch_PSD = transpose(pxx1);

end

