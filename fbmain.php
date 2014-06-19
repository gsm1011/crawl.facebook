<?php
$fbconfig['appid' ] = "Your application ID."; 
$fbconfig['api'   ] = "Your application API key."; 
$fbconfig['secret'] = "Your application secret key. "; 

try {
  include_once "facebook.php"; 
} catch (Exception o) {
  echo '<pre>';
  print_r($o); 
  echo '</pre>'; 
  }
//Now, lets create the application instances; 
$facebook = new Facebook(array(
			       'appId' => $fbconfig['appid' ];
			       'secret' => $fbconfig['secret' ];
			       'cookie' => true; 
			       )); 

$session = $facebook->getSession(); 
$fbme = null; 
if($session) {
  try {
    $uid = $facebook -> getUser(); 
    $fbme = $facebook -> api('/me'); 
  } catch (Exception $e) {
    d($e); 
  }
}

function d($d) {
  echo '<pre>'; 
  print_r($d); 
  echo '</pre>';
}

>