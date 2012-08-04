<?php
	require_once('common/config.php');

	if (isset($_REQUEST['test'])) {
		
	}
	else {
		loadModel();
		loadView('home');

		$model = new model;
		$view = new homePage;

		$result = $model->parse_json('test.json');
		$view->gen_page($result);
	}
?>