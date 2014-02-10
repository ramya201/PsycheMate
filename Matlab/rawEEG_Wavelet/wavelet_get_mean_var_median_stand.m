
%level 6, coef =13  
%level 7, coef =35  

levels=7 % wavelet tree total level numbers
coefTotal=25 %total coeficients used on the last tree level for feature extraction
 
cont=1

for sampleN=1:40
    
    type(1,cont)=1;
    for coefN=1:coefTotal
       sample=ThoughtB{:,sampleN};
       tree=wpdec(sample,levels,'db1','shannon');
       signal=wpcoef(tree,[levels coefN]);
       matrix(coefN,cont)=mean(signal);
       matrix(coefN+coefTotal,cont)=var(signal);
       matrix(coefN+coefTotal*2,cont)=std(signal);
       matrix(coefN+coefTotal*3,cont)=median(signal); 
    end
    cont=cont+1;
    
    
    type(2,cont)=1;
    for coefN=1:coefTotal
       sample=ThoughtE{:,sampleN};
       tree=wpdec(sample,levels,'db1','shannon');
       signal=wpcoef(tree,[levels coefN]);
       matrix(coefN,cont)=mean(signal);
       matrix(coefN+coefTotal,cont)=var(signal);
       matrix(coefN+coefTotal*2,cont)=std(signal);
       matrix(coefN+coefTotal*3,cont)=median(signal);
    end
    cont=cont+1;

     
    type(3,cont)=1;
    for coefN=1:coefTotal
       sample=ThoughtF{:,sampleN};
       tree=wpdec(sample,levels,'db1','shannon');
       signal=wpcoef(tree,[levels coefN]);
       matrix(coefN,cont)=mean(signal);
       matrix(coefN+coefTotal,cont)=var(signal);
       matrix(coefN+coefTotal*2,cont)=std(signal);
       matrix(coefN+coefTotal*3,cont)=median(signal);  
    end
    cont=cont+1;



end

 
 