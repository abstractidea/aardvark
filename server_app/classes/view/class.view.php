<?php
	abstract class page {
		private function gen_header() {
			echo '
				<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
				<html>
					<head>
						<title>Project Aardvark</title>

						'.loadCSS('main').'
						'.loadJS('main').'
			';
		}
		private function gen_precontent() {
			echo '
					</head>
					<body>
						<div id="wrapper">
							<div id="header">
							</div>
							<div id="nav">
								<ul id="navigation">
									<li><a href="'.WEB_ROOT.'">Home</a></li>
									<li><a href="'.WEB_ROOT.'#">Nothing</a></li>
									<li><a href="'.WEB_ROOT.'#">Nothing</a></li>
									<li><a href="'.WEB_ROOT.'#">Nothing</a></li>
									'.loadSettings().'
								</ul>
							</div>
							<div id="content">
			';
		}
		
		abstract function gen_content();

		private function gen_footer() {
			echo '
							</div>
							<div id="footer">
								<span>Copyright &copy; 2002 - '.date('Y').' Team of Project Aardvark</span>
							</div>
						</div>
					</body>
				</html>
			';
		}
		public function gen_page($result='') {
			$this->gen_header();
			$this->gen_precontent();
			$this->gen_content($result);
			$this->gen_footer();
		}
	}
?>