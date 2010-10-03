/**
 * Javascript for the create / edit profile pages.
 */

var macademia = macademia || {};

macademia.allowedDomains = [ 'macalester.edu' ];


macademia.initializeRegister = function() {
  macademia.upload.init();
  macademia.links.init();
  macademia.initAnalyzeInterests();
  macademia.autocomplete.initEditProfile();

    $("#cancelAccountCreation").click(function() {
       $("#registerDialog").jqmHide(); 
    })

    $('#edit_profile').submit(function() {
      try {
          $(".warning").hide();
          $(this).serialize();
          macademia.links.serialize();

          var name = $(this).find('[name=fullName]').val();
          var department = $(this).find('[name=department]').val();
          var hasError = false;

          if (name.length < 5) {
              $('#nameErrors').html("<b>Name must be provided</b>");
              $('#nameErrors').show();
              hasError=true;
          }

          // If we are in edit profile skip password and email
          if (macademia.isNewUser()) {
              var pass = $(this).find('[name=pass]').val();
              var confirm = $(this).find('[name=passConfirm]').val();
              var email = $(this).find('[name=email]').val();
              if (pass!=confirm) {
                  $("#passConfirmErrors").html("<b>Passwords do not match</b>");
                  $("#passConfirmErrors").show();
                  hasError=true;
              }
              if (pass.length<6) {
                  $("#passErrors").html("<b>Password must be at least six characters</b>");
                  $("#passErrors").show();
                  hasError=true;

              }
              if (email.length<5) {
                  $('#emailErrors').html("<b>Valid email must be provided</b>");
                  $('#emailErrors').show();
                  hasError=true;
              } else {
                  var domain = email.split('@')[1];
                  if ($.inArray(domain, macademia.allowedDomains) < 0) {
                      $('#emailErrors').html("<b>Only " + macademia.allowedDomains.join(", ") + " email addresses allowed</b>");
                      $('#emailErrors').show();
                      hasError=true;
                  }
              }
          }
          if (hasError) {
              $('html, body').animate({scrollTop:0}, 'slow');
          } else {
              $("#submit_edits").hide();
              var interests = $('#editInterests').val().split(',');
              macademia.analyzeInterests(interests, 0, $(".progressBar"), macademia.saveUserProfile);
          }

      } catch(err) {
          alert('profile submission failed: ' + err);
      }
      return false;
  });
};

/**
 * Returns true if we are showing the create user dialog, false otherwise.
 */
macademia.isNewUser = function() {
    return ($('#edit_profile').find('[name=pass]').length > 0);
};

macademia.saveUserProfile = function() {
   var url = '/Macademia/account/' + (macademia.isNewUser() ? 'saveuser' : 'updateuser');
   jQuery.ajax({
          url: url,
          type: "POST",
          data: $('#edit_profile').serialize(),
          dataType: "text",
          success: function(data) {
              try {
                  if (data && data.substring(0, 5) == 'okay ') {
                       macademia.reloadToPerson(data.substring(5));
                       return;
                  }
                  macademia.initAnalyzeInterests();
                  var showedErrors = false;
                  if (data.indexOf('Email') == 0) {
                      showedErrors = true;
                      $('#emailErrors').html("<b>" + data + "</b>");
                      $('#emailErrors').show();
                  }
                  if (data.indexOf('You') == 0) {
                      showedErrors = true;
                      $('#passErrors').html("<b>" + data + "</b>");
                      $('#passErrors').show();
                  }
                  if (!showedErrors) {
                      $('#nameErrors').html("<b>" + macademia.htmlEncode(data) +"</b>");
                      $('#nameErrors').show();
                  }
                  $('#registerDialog').animate({scrollTop:0}, 'slow');
              } catch (err) {
                  alert('error occurred after saving user: ' + err);
                  macademia.initAnalyzeInterests();
                  return;
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
            if (value.substr(0, 7) != 'http://') {
                value = 'http://' + value;
            }
            linkStr += "<li><a href=\"" + encodeURI(value) + "\">";
            linkStr += macademia.htmlEncode(name) + "</a>\n";
        }
    });
    $(".personLinks input[name='links']").val(linkStr);
};

macademia.links.deserialize = function() {
    try {
    var linksStr =$(".personLinks input[name='links']").val();
    $(linksStr).find('a').each(
        function() {
            macademia.links.addNewLink($(this).text(), decodeURI($(this).attr('href')));
        });
    } catch (err) {
        alert('error during link deserialization: ' + err);
    }

};
