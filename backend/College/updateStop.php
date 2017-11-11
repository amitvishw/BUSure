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
    $emailID    = $_POST['emailID'];
    $stop       = $_POST['stop'];
    $bus1       = $_POST['bus1'];
    $bus2       = $_POST['bus2'];
    $bus3       = $_POST['bus3'];
    
    $stmt       = $conn->prepare('UPDATE stops SET bus1=?,bus2=?,bus3=? WHERE stop=? AND collegeid=?');
    
    if(!$stmt)
    {
        $response['success'] = 0;
        $response['message'] = 'Oops! An error occurred.';
        echo json_encode($response);
        die();
    }

    $stmt       ->bind_param('iiiss',$bus1,$bus2,$bus3,$stop,$emailID);
    $stmt       ->execute();
    
    if($stmt)
    {
        $response['success'] = 1;
        $response['message'] = 'Stop successfully Updated.';
        echo json_encode($response);
    }
    else
    {
        $response['success'] = 0;
        $response['message'] = 'Oops! An error occurred.';
        echo json_encode($response);
    }
?>