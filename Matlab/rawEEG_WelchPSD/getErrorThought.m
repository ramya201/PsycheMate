% Calculates the misclassification percentage rate for
% Support Vector Machine algorithm.
% 10-fold cross-validation is used
% X - Input feature matrix (No of samples x No of features)
% Y - Target Classification matrix (No of samples x 1, values ranging from 1:k where k is no of classes)
% k - No of Classes

function [errorRate,confusion_matrix] = getErrorThought(X, Y, k)
errorPercent = 0;


for i = 1:k
    for j = 1:k
        confusion_matrix(i,j) = 0;
    end
end

noRows = size(X,1);
CVO = cvpartition(noRows,'k',10);

for i = 1:CVO.NumTestSets
    errors = 0;
    
    trIdx = CVO.training(i);
    teIdx = CVO.test(i);
    
    trainX = X(trIdx,:);
    trainY = Y(trIdx,:);
    testX = X(teIdx,:);
    testY = Y(teIdx,:);
    
    SVMStruct = svmtrain(trainX, trainY);
    predictedY = svmclassify(SVMStruct, testX);
    
    noTestRows = size(testX,1);
    
    for j = 1:noTestRows
        
        confusion_matrix(predictedY(j,:),testY(j,:)) = confusion_matrix(predictedY(j,:),testY(j,:)) + 1;
        
        if (predictedY(j,:) ~= testY(j,:))
            errors = errors + 1;
        end        
    end
    
    temp = ((errors/noTestRows)*100);
    errorPercent = errorPercent + temp;
end

errorRate = errorPercent/CVO.NumTestSets;
end

