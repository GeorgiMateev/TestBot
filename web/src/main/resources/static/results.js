$(function () {

	$(document).ready(function() {
	  $('pre code').each(function(i, block) {
	    hljs.highlightBlock(block);
	  });
	});

	var hardcodedDomParts = [
		['<div>dio<button>makq</button></dio>', '<div><button>makq</button>udri</dio>'],
		['<div><span>kor</span></div>', '<div><span>hoi</span></div>'],
		['<div><span>ui</span></div>', '<div><span>ui</span>']
	];

	$('#start-testing-button').click(function() {
		var resultsPanel = $('#results-panel');
		for (var i = 0; i < hardcodedDomParts.length; i++) {
			resultsPanel.append('<table id="warnings-table-'+i+'">'+
	          '<tr>'+
	            '<td height=100 contenteditable="true" class="left"><pre><code class="html left"></code></pre></td>'+
	            '<td height=100 contenteditable="true" class="right"><pre><code class="html right"></code></pre></td>'+
	            '<td height=100 contenteditable="true" class="result"></td>'+
	          '</tr>'+
	        '</table><button type="button" class="btn btn-info check-code-button">Check</button>'+
	        '<div class="panel panel-default" style="display: none;">'+
  				'<div class="panel-body">'+
		        	'<span style="width: 50%;">'+hardcodedDomParts[i][0]+'</span>'+
		        	'<span>'+hardcodedDomParts[i][1]+'</span>'+
  				'</div>'+
	        '</div>');
	        $('#warnings-table-'+i).find('.left .left').text(hardcodedDomParts[i][0]).html();
	        $('#warnings-table-'+i).find('.right .right').text(hardcodedDomParts[i][1]).html();
	        $('#warnings-table-'+i).next().click(function() {
	        	$(this).next().show();
	        })
		}
		// Make diffs and put them in the third
		var warningTables = $('#results-panel table');
		warningTables.each(function(index, warningTable) {
			var leftText = $(warningTable).find('.left .left').text(),
				rightText = $(warningTable).find('.right .right').text();
			console.log(leftText);
			console.log(rightText);
			var diff = JsDiff['diffChars'](leftText, rightText);
			var result = $(warningTable).find('.result')[0];
			var fragment = document.createDocumentFragment();

			for (var i=0; i < diff.length; i++) {

				if (diff[i].added && diff[i + 1] && diff[i + 1].removed) {
					var swap = diff[i];
					diff[i] = diff[i + 1];
					diff[i + 1] = swap;
				}

				var node;
				if (diff[i].removed) {
					node = document.createElement('del');
					node.appendChild(document.createTextNode(diff[i].value));
				} else if (diff[i].added) {
					node = document.createElement('ins');
					node.appendChild(document.createTextNode(diff[i].value));
				} else {
					node = document.createTextNode(diff[i].value);
				}
				fragment.appendChild(node);
			}

			result.textContent = '';
			result.appendChild(fragment);

		});
	})




	// var left = warnings[0],
	// 	right = warnings[1],
	// 	result = warnings[2];
	// var diff = JsDiff['diffChars'](left.textContent, right.textContent);
	// var fragment = document.createDocumentFragment();

	// for (var i=0; i < diff.length; i++) {

	// 	if (diff[i].added && diff[i + 1] && diff[i + 1].removed) {
	// 		var swap = diff[i];
	// 		diff[i] = diff[i + 1];
	// 		diff[i + 1] = swap;
	// 	}

	// 	var node;
	// 	if (diff[i].removed) {
	// 		node = document.createElement('del');
	// 		node.appendChild(document.createTextNode(diff[i].value));
	// 	} else if (diff[i].added) {
	// 		node = document.createElement('ins');
	// 		node.appendChild(document.createTextNode(diff[i].value));
	// 	} else {
	// 		node = document.createTextNode(diff[i].value);
	// 	}
	// 	fragment.appendChild(node);
	// }

	// result.textContent = '';
	// result.appendChild(fragment);

});
