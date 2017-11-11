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
        $response['success']    = 0;
        $response['message']    = 'Ops! Can not connect to database';
        echo json_encode($response);
        die();
    }

    $emailID    = $_POST['emailID'];
    $stop       = $_POST['stop'];
    $bus1       = $_POST['bus1'];
    $bus2       = $_POST['bus2'];
    $bus3       = $_POST['bus3'];
    
    
    $stmt       = $conn->prepare('INSERT INTO stops (collegeid,stop,bus1,bus2,bus3) VALUES (?,?,?,?,?)');
    
    if(!$stmt)
    {
        $response['success']    = 0;
        $response['message']    = 'Oops! An error occurred.stmt';
        echo json_encode($response);
        die();
    }

    $stmt       ->bind_param('ssiii',$emailID,$stop,$bus1,$bus2,$bus3);
    $stmt       ->execute();
    
    if($stmt)
    {
        $response['success']    = 1;
        $response['message']    = 'Stop successfully Added.';
        echo json_encode($response);
    }
    else
    {
        $response['success']    = 0;
        $response['message']    = 'Oops! An error occurred.last';
        echo json_encode($response);
    }
?>