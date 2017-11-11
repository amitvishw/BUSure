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
    

    $emailID    = $_POST['cEmailID'];
    $option     = $_POST['option'];

    $sql        = 'SELECT * FROM stops WHERE collegeid = "'.$emailID.'" ORDER BY stop';

    $result     = mysqli_query($conn, $sql);
    
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
        $response['message']  = 'No stop details are available.';
        echo json_encode($response);
        die();
    }
    $response['success']    = 1;
    $emparray               = array();
    
    while($row = mysqli_fetch_object($result))
    {
        $emparray[]         = $row;
    }
    $response['stops']      = $emparray;
    echo json_encode($response);
?>
