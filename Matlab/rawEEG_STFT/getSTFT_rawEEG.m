function [STFT_rawEEG] = getSTFT_rawEEG(cell,j)
X1 = cell{:,j};

pxx1 = spectrogram(X1, 250, [], [1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50], 512);

[a,b] = size(pxx1)
%STFT_rawEEG = reshape(abs(real(pxx1)), 1, a * b);

STFT_rawEEG = reshape(abs(pxx1), 1, a * b);

end