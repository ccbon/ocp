<?php 

function reset_func($method_name, $params, $app_data) {
	unlink('sponsor');
	$base = new SQLiteDatabase('sponsor', 0666, $err);
	$query = "CREATE TABLE sponsor (
            url longtext
            )";
            
	$results = $base->queryexec($query);
	return 'reset';
}


function list_func($method_name, $params, $app_data) {
	$base = new SQLiteDatabase('sponsor', 0666, $err);
	$query = "SELECT url FROM sponsor";
	$results = $base->arrayQuery($query, SQLITE_ASSOC);
	return $results;
}

function add_func($method_name, $params, $app_data) {
	$ip = $_SERVER['REMOTE_ADDR'];
	$url = "tcp://" . $ip . ":" . $params[0];
	$base = new SQLiteDatabase('sponsor', 0666, $err);
	$query = "INSERT INTO sponsor (url) VALUES ('$url')";    
	$results = $base->queryexec($query);
	
	return 'ok added';
}


$xmlrpc_server = xmlrpc_server_create();


xmlrpc_server_register_method($xmlrpc_server, "reset", "reset_func");

xmlrpc_server_register_method($xmlrpc_server, "list", "list_func");

xmlrpc_server_register_method($xmlrpc_server, "add", "add_func");

$request_xml = $HTTP_RAW_POST_DATA;

$response = xmlrpc_server_call_method($xmlrpc_server, $request_xml, '');

print $response;

xmlrpc_server_destroy($xmlrpc_server);

?>

