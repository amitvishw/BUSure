<?php
	$response     = array();
    $name		  = $_POST['name'];
    $image 	      = $_POST['image'];
    
    $name         = str_replace("@","_",$name);
    $name         = str_replace(".","_",$name);
    $decodedImage = base64_decode($image);
    
    if(!$decodedImage)
    {
    	$response['success'] = 0;
      	$response['message'] = 'Oops! An error occurred.';
      	echo json_encode($response);
      	die();
    }
    
    file_put_contents('images/'.$name.'.PNG',$decodedImage);
    $response['success'] = 1;
    $response['message'] = 'Image Uploaded';
    echo json_encode($response);
?>
