/**
 * Javascript for the create / edit profile pages.
 */

var macademia = macademia || {};


macademia.initializeModalRegister = function() {
  macademia.upload.init();
  macademia.links.init();
  macademia.initAnalyzeInterests();
  $("#passwordpolicy").hide();
  $("#passwordpolicybtn").bt({
      contentSelector: $("#passwordpolicy"),
      width: '350px',
      closeWhenOthersOpen: true,
      shrinkToFit: 'true',
      positions: ['right', 'top', 'left'],
      margin: 0,
      padding: 6,
      fill: '#fff',
      strokeWidth: 1,
      strokeStyle: '#c2c2c2',
      spikeGirth: 12,
      spikeLength:9,
      hoverIntentOpts: {interval: 100,
                        timeout: 1000}
  });

  $('#edit_profile_container form').submit(function() {
      $(this).serialize();
      macademia.links.serialize();

      var name = $(this).find('[name=fullName]').val();
      var pass = $(this).find('[name=pass]').val();
      var confirm = $(this).find('[name=passConfirm]').val();
      var email = $(this).find('[name=email]').val();
      var department = $(this).find('[name=department]').val();
      var formCheck = true;

      if (name.length < 5) {
          $('#nameErrors').html("<b>Name must be provided</b>");
          $('#nameErrors').show();
          formCheck=false;
      }

      // If we are in edit profile skip password and email
      if (macademia.isNewUser()) {
          if (pass!=confirm) {
              $("#passConfirmErrors").html("<b>Passwords do not match</b>");
              $("#passConfirmErrors").show();
              formCheck=false;
          }
          if (pass.length<6) {
              $("#passErrors").html("<b>Password must be at least six characters</b>");
              $("#passErrors").show();
              formCheck=false;

          }
          if (email.length<5) {
              $('#emailErrors').html("<b>Valid email must be provided</b>");
              $('#emailErrors').show();
              formCheck=false;
          }
      }

      if (department.length<3) {
          $('#deptErrors').html("<b>Department must be provided</b>");
          $('#deptErrors').show();
          formCheck=false;
      }

      if (formCheck) {
          $("#submit_edits").hide();
          var interests = $('#editInterests').val().split(',');
          macademia.analyzeInterests(interests, 0, $("#edit_profile_container .progressBar"), macademia.saveUserProfile);
      } else {
          $('#registerDialog').animate({scrollTop:0}, 'slow');  
      }
      return false;
  });
};

/**
 * Returns true if we are showing the create user dialog, false otherwise.
 */
macademia.isNewUser = function() {
    return $('#edit_profile_container form').find('[name=pass').is(':visibile');
};

macademia.saveUserProfile = function() {
   var url = '/Macademia/account/' + (macademia.isNewUser() ? 'saveuser' : 'updateuser');
   jQuery.ajax({
          url: url,
          type: "POST",
          data: $('#edit_profile_container form').serialize(),
          dataType: "text",
          success: function(data) {
              if (data == 'okay') {
                    window.location.reload();                   
              }
              macademia.initAnalyzeInterests();
              if (data.indexOf('Email') == 0) {
                  $('#emailErrors').html("<b>" + data + "</b>");
                  $('#emailErrors').show();
                  $('#registerDialog').animate({scrollTop:0}, 'slow');
              }
              if (data.indexOf('You') == 0) {
                  $('#passErrors').html("<b>" + data + "</b>");
                  $('#passErrors').show();
                  $('#registerDialog').animate({scrollTop:0}, 'slow');
              }
          }, 
          error: function(request, status, errorThrown) {
              macademia.initAnalyzeInterests();
              alert('error occurred when saving user: ' + status + ', ' + errorThrown);
              return;
          }
      });
};

macademia.initAnalyzeInterests = function() {
    $("#submit_edits").show();      // in case we are recovering from a submission error.
    $(".progressBar").hide();
    $(".progressBarCaption").hide();
    $(".progressBar").progressbar({ value : 10});
};

macademia.analyzeInterests = function(interests, index, progressBar, callback) {
    if (index >= interests.length) {
        return callback();
    }
    progressBar.show();
    var i = interests[index];
    progressBar.progressbar('value', 100 * (index+1) / interests.length);
    progressBar.find("span").text("learning about '" + i + "'");
    jQuery.ajax({
          url: '/Macademia/interest/analyze/',
          type: "POST",
          data: {interest : i},
          dataType: "text",
          success: function(data) {
              var relatedPage = data;   // not using this for now.
              return macademia.analyzeInterests(interests, index+1, progressBar, callback);
          },
          error: function(request, status, errorThrown) {
              macademia.initAnalyzeInterests();
              alert('error occurred when processing interest ' + i + ': ' + status + ', ' + errorThrown);
              return;
          }
      });
};

macademia.loginShowHide = function() {
		$("#login").hide();
		$("#login_link").click(function(event) {
			$("#login").slideToggle();
		});
};

// Code for managing the links on the edit page.
macademia.links = {};
macademia.links.init = function() {
    $(".personLinks .addLink").click(
            function () {return macademia.links.addNewLink();}
        );
    macademia.links.deserialize();
    $(".personLinks .clearDefault").clearDefault();
    macademia.links.addNewLink();
};

macademia.links.addNewLink = function(linkName, linkUrl) {
    var newDiv = $(".personLinks .customLinkTemplate").clone();
    newDiv.removeClass("customLinkTemplate");
     if (linkName) {
        newDiv.find('.linkField input').val(linkName);
    }
    if (linkUrl) {
        newDiv.find('.linkValue input').val(linkUrl);
    }

    $(".personLinks .example").before(newDiv);

    newDiv.find(".removeLink").click(
                function () {
                    $(this).parent().parent().remove();
                    return false;
                }
            );
    newDiv.find('.clearDefault').clearDefault();
    newDiv.show();
    return false;
};

macademia.links.serialize = function() {
    var linkStr = "";
    $(".personLinks .customLink").each(function () {
        var name;
        // handles custom and default named fields separately.
        if ($(this).find('.linkField input').length > 0) {
            name = $(this).find('.linkField input').val();
        } else {
            name = $(this).find('.linkField').html();
        }
        var value= $(this).find('.linkValue input').val();
        if (value && value != $(this).find('.linkValue input').attr('prompt')) {
            linkStr += "<li><a href=\"" + encodeURI(value) + "\">";
            linkStr += macademia.htmlEncode(name) + "</a>\n";
        }
    });
    $("#edit_profile_container input[name='links']").val(linkStr);
};
macademia.links.deserialize = function() {
    try {
    var linksStr =$("#edit_profile_container input[name='links']").val();
    $(linksStr).find('a').each(
        function() {
            macademia.links.addNewLink($(this).text(), decodeURI($(this).attr('href')));
        });
    } catch (err) {
        alert('error during link deserialization: ' + err);
    }

};