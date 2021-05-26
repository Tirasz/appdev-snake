

function showSP(){
    $("#sp-container").css("display", "block");
    $("#mp-container").css("display", "none");
    document.cookie = "last=SP"
}

function showMP(){
    $("#sp-container").css("display", "none");
    $("#mp-container").css("display", "block");
    document.cookie = "last=MP"
}



function init(){
    if(document.cookie === "last=SP"){
        showSP();
    }else{
        showMP();
    }
}