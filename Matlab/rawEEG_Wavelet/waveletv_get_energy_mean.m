
 
levels=7 % wavelet tree total level numbers
cont=1
coefTotal=25 %total coeficients used on the last tree level for feature extraction
 
for sampleN=1:40
    
    type(1,cont)=1;
    for coefN=1:coefTotal
       sample=ThoughtB{:,sampleN};
       tree=wpdec(sample,levels,'db1','shannon');
       signal=wpcoef(tree,[levels coefN]);
       matrix(coefN,cont)=mean(signal);
       matrix(coefN+coefTotal,cont)=sum(signal.^2)
    end
    cont=cont+1;
    
    
    type(2,cont)=1;
    for coefN=1:coefTotal
       sample=ThoughtE{:,sampleN};
       tree=wpdec(sample,levels,'db1','shannon');
       signal=wpcoef(tree,[levels coefN]);
       matrix(coefN,cont)=mean(signal);
       matrix(coefN+coefTotal,cont)=sum(signal.^2) ; 
    end
    cont=cont+1;

     
    type(3,cont)=1;
    for coefN=1:coefTotal
       sample=ThoughtF{:,sampleN};
       tree=wpdec(sample,levels,'db1','shannon');
       signal=wpcoef(tree,[levels coefN]);
       matrix(coefN,cont)=mean(signal);
       matrix(coefN+coefTotal,cont)=sum(signal.^2);  
    end
    cont=cont+1;



end

 
 