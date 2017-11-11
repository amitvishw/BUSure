<?php
    //error_reporting(0);
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
    $stop       = $_POST['stop'];
    $college    = $_POST['college'];
    $cEmailID   = $_POST['cEmailID'];
    $password   = $_POST['password'];
    $emailID    = $_POST['emailID'];
    
    $stmt       = $conn->prepare('INSERT INTO student (name,stop,college,cemail,password,email) VALUES (?,?,?,?,?,?)');

    if(!$stmt)
    {
        $response['success'] = 0;
        $response['message'] = 'Oops! An error occurred.';
        echo json_encode($response);
        die();
    }

    $stmt       ->bind_param('ssssss',$name,$stop,$college,$cEmailID,$password,$emailID);
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
