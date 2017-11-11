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
        $response['success']=0;
        $response['message']='Ops! Can not connect to database';
        echo json_encode($response);
        die();
    }
    

    $emailID     = $_POST['cEmailID'];
    $defaultStop = $_POST['stop'];
    
    $sql    ='SELECT * FROM stops WHERE collegeid = "'.$emailID.'" ORDER BY stop';
    $sql_2  ='SELECT * FROM stops WHERE collegeid = "'.$emailID.'" AND stop = "'.$defaultStop.'"';

 
    $result     = mysqli_query($conn, $sql);
    $result_2   = mysqli_query($conn, $sql_2);
    
    if(!$result && $result_2)
    {
      $response['success']  =0;
      $response['message']  ='Oops! An error occurred.';
      echo json_encode($response);
      die();
    }
    
    if(mysqli_num_rows($result)<=0 || mysqli_num_rows($result_2)<=0)
    {
      $response['success']  =0;
      $response['message']  ='No stop details are available.';
      echo json_encode($response);
      die();
    }
    $response['success']    =1;
    $emparray               = array();
    $dStop                  = array();
    
    while($row=mysqli_fetch_object($result))
    {
        $emparray[]=$row;
    }
    while($row=mysqli_fetch_object($result_2))
    {
        $dStop['stop']    = $row->stop;
        $dStop['bus1']    = $row->bus1;
        $dStop['bus2']    = $row->bus2;
        $dStop['bus3']    = $row->bus3;
    }
    $response['dStop']      =$dStop;
    $response['stops']      =$emparray;
    echo json_encode($response);
?>
