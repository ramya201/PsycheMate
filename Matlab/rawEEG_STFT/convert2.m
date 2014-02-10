cont=1;

for i=1:40
%     matrix2(:,cont)=getSTFT_rawEEG(ThoughtA,i);
%     type2(1,cont)=1;
%     cont=cont+1;
    
    matrix2(:,cont)=getSTFT_rawEEG(ThoughtB,i);
    type2(1,cont)=1;
    cont=cont+1;
    
%     matrix2(:,cont)=getSTFT_rawEEG(ThoughtC,i);
%     type2(,cont)=1;
%     cont=cont+1;
    
    
%     matrix2(:,cont)=getSTFT_rawEEG(ThoughtD,i);   
%     type2(4,cont)=1;
%     cont=cont+1;

    matrix2(:,cont)=getSTFT_rawEEG(ThoughtE,i);
    type2(2,cont)=1;
    cont=cont+1;
    
    matrix2(:,cont)=getSTFT_rawEEG(ThoughtF,i);
    type2(3,cont)=1;
    cont=cont+1;
end
matrix2t=transpose(matrix2);
[u,s,v]=svds(matrix2t,100);

input=transpose(u)