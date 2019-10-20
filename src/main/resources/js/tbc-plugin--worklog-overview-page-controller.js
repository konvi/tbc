AJS.toInit(function(){
	AJS.log('KDP: Overview Page Controller initializing ...');
	var baseUrl = AJS.params.baseURL;
	var restUrl = baseUrl + '/rest/tbc-page/1.0';
	window.KDPrestUrl = restUrl;

	// Init Base SOY template
	var overviewPageTemplate = JIRA.Templates.TBCO.overviewPage();
	AJS.$('#kdp-overview-page-container').html(overviewPageTemplate);

	AJS.$.ajax({
		url: window.KDPrestUrl + '/tbc_page',
		dataType: 'json',
		success: function(data) {
			const items = data.items;
			const summary = data.summary;
			var table = '<table class="aui"><thead>';
			if (items !== 'undefined' && items.length > 0) {
				table += "<td>Item</td><td>Estimation</td>";
				var devs = items[0].devs;
				for (var i = 0; i < devs.length; i++) {
					table += "<td>" + devs[i] + "</td>";
				}
			}

			// var events = [];
			table += "</thead><tbody>";
			AJS.$(items).each(function() {
				var item = AJS.$(this).attr('item');
				var estimation = AJS.$(this).attr('estimation');
				var devs = AJS.$(this).attr('devs');
				var loggedByDev = AJS.$(this).attr('loggedByDev');
				table += "<tr><td>" + item + "</td><td>" + (estimation !== undefined ? estimation : '') + "</td>";
				for (var i = 0; i < devs.length; i++) {
					if (devs[i] in loggedByDev) {
						const logged = loggedByDev[devs[i]];
						table += "<td class='" + logged.activity + "'>" + logged.time + "</td>";
					} else {
						table += "<td></td>";
					}
				}
				table += "</tr>";
				/*events.push({
					title: users.join(', '),
					start: AJS.$(this).attr('start'),
					end: AJS.$(this).attr('end'),
					color: users.length > 0 ? '#36B37E' : '#FFAB00',
				});*/
			});

			table += "</tbody><tfoot>";

			// totals
			AJS.$(summary).each(function() {
				var item = AJS.$(this).attr('item');
				var estimation = AJS.$(this).attr('estimation');
				var devs = AJS.$(this).attr('devs');
				var loggedByDev = AJS.$(this).attr('loggedByDev');
				table += "<tr><td>" + item + "</td><td>" + (estimation !== undefined ? estimation : '') + "</td>";
				for (var i = 0; i < devs.length; i++) {
					table += "<td>" + ((devs[i] in loggedByDev) ? loggedByDev[devs[i]].time : '') + "</td>";
				}
				table += "</tr>";
			});

			table += "</tfoot></table>";
			AJS.$('#tbc-overview').html(table);
			//callback(events);
		}
	});

});