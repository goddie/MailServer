function readFiles(evt){ 
var files=evt.target.files; 
if(!files){ 
console.log("the file is invaild"); 
return; 
} 
for(var i=0, file; file=files[i]; i++){ 
var imgele=new Image(); 
var thesrc=window.URL.createObjectURL(file); 
imgele.src=thesrc; 
imgele.onload=function(){ 
$("#showlogo").attr("src",this.src); 
} 
} 
} 