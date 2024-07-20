console.log("admin user");


document.querySelector("#image_file_input").addEventListener('change',function(event){
  
    let file = event.target.files[0];
    let reader = new FileReader();
    reader.onload = function(){
        // document.getElementById("uploadImagePreview").src = reader.result;
        document.querySelector("#uploadImagePreview").setAttribute("src", reader.result);
    };
    reader.readAsDataURL(file);

})