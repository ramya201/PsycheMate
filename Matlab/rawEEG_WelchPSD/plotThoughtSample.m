function [] = plotThoughtSample(cell,j)

Y1 = cell{1,:,j};

Y2 = cell{2,:,j};

Y3 = cell{3,:,j};

Y4 = cell{4,:,j};

Y5 = cell{5,:,j};

Y6 = cell{6,:,j};

Y7 = cell{7,:,j};

Y8 = cell{8,:,j};

X = 1:15

wvtool(X,Y1,X,Y2,X,Y3,X,Y4,X,Y5,X,Y6,X,Y7,X,Y8)

end

