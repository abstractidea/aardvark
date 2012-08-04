<?php
	// Global Functions
	function loadController($name='') {
		if ($name=='') {
			require_once(CLASSES.'controller/class.controller.php');
		}
		else {
			require_once(CLASSES.'controller/class.controller.'.$name.'.php');
		}
	}
	function loadModel($name='') {
		if ($name=='') {
			require_once(CLASSES.'model/class.model.php');
		}
		else {
			require_once(CLASSES.'model/class.model.'.$name.'.php');
		}
	}
	function loadView($name='') {
		if ($name=='') {
			require_once(CLASSES.'view/class.view.php');
		}
		else {
			require_once(CLASSES.'view/class.view.'.$name.'.php');
		}
	}
	function loadCSS($name='') {
		return '<link rel="stylesheet" type="text/css" href="'.RESOURCES.'css/'.$name.'.css" />';
	}
	function loadJS($name='') {
		return '<script type="text/javascript" src="'.RESOURCES.'js/'.$name.'.js"></script>';
	}
?>