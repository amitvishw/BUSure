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
    
    $sql        = 'SELECT name,email FROM college ORDER BY name';
    $result     =  mysqli_query($conn, $sql);
    
    if(!$result)
    {
        $response['success']  = 0;
        $response['message']  = 'Oops! An error occurred.';
        echo json_encode($response);
        die();
    }
    if(mysqli_num_rows($result)<=0)
    {
        $response['success']  = 0;
        $response['message']  = 'No college are available.';
        echo json_encode($response);
        die();
    }
    $response['success']      = 1;
    $emparray                 = array();
    
    while($row=mysqli_fetch_object($result))
    {
        $emparray[]           = $row;
    }
    $response['college']      = $emparray;
    echo json_encode($response);
?>
