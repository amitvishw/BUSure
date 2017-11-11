<?php
    error_reporting(0);
    $response   = array();
    $servername = 'localhost';
    $username   = 'root';
    $password   = '';
    $dbname     = 'busure';
    $conn       = new mysqli($servername, $username, $password,$dbname);
    if($conn->connect_error)
    {
        $response['success'] = 0;
        $response['message'] = 'Ops! Can not connect to database';
        echo json_encode($response);
        die();
    }
    $name       = $_POST['name'];
    $city       = $_POST['city'];
    $state      = $_POST['state'];
    $pinCode    = $_POST['pinCode'];
    $phone      = $_POST['phone'];
    $emailID    = $_POST['emailID'];
    $password   = $_POST['password'];


    $stmt       = $conn->prepare('INSERT INTO college (name,city,state,pincode,phone,email,password) VALUES (?,?,?,?,?,?,?)');
    
    if(!$stmt)
    {
        $response['success'] = 0;
        $response['message'] = 'Oops! An error occurred.';
        echo json_encode($response);
        die();
    }

    $stmt       ->bind_param('sssisss',$name,$city,$state,$pinCode,$phone,$emailID,$password);
    $stmt       ->execute();
    
    if($stmt)
    {
        $response['success'] = 1;
        $response['message'] = 'Account successfully created.';
        echo json_encode($response);
    }
    else
    {
        $response['success'] = 0;
        $response['message'] = 'Oops! An error occurred.';
        echo json_encode($response);
    }
?>

