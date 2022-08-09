CKEDITOR.replace('description');

function getData(){
    var desc_data = CKEDITOR.instances['description'].getData();
    return desc_data;
}

