<?php
	//error_reporting(0);
    $response   = array();
	$headers    = "MIME-Version: 1.0" . "\r\n";
	$headers   .= "Content-type:text/html;charset=UTF-8" . "\r\n";
	$headers   .= 'From: CBus <noreply@bit2hex.xyz>' . "\r\n";
	$return	    = '-fnoreply@bit2hex.xyz';
	
    $response   = array();
    $servername = 'localhost';
    $username   = 'root';
    $password   = '';
    $dbname     = 'busure';
    $conn       = new mysqli($servername, $username, $password,$dbname);
    if($conn->connect_error)
    {
        $response['success']=0;
        $response['message']='Ops! Can not connect to database';
        echo json_encode($response);
        die();
    }
    $emailID    =$_POST['emailID'];
    $stmt       =$conn->prepare('UPDATE college SET OTP=? where email=?');
    if(!$stmt)
    {
        $response['success']    =0;
        $response['message']    ='Oops! An error occurred.';
        echo json_encode($response);
        die();
    }
    $code       = rand(100000,999999);
    $stmt       ->bind_param('is',$code,$emailID);
    $stmt       ->execute();

    if($stmt)
    {
    	$message  = 'Thank you for signing up with us. Your Account has been 
    				created successfully to activate your account enter following code '.$code;
		if(mail($emailID,'CBus Email Confirmation',$message,$headers,$return))
		{
			$response['success'] = 1;
        	$response['message'] = 'Email Has Been Sent';
        	echo json_encode($response);
       		die();
		}
		else
		{
			$response['success'] = 0;
        	$response['message'] = 'Oops! An error occurred';
		}
    }
    else
    {
        $response['success'] = 0;
        $response['message'] = 'Oops! An error occurred.';
        echo json_encode($response);
    }
?>
