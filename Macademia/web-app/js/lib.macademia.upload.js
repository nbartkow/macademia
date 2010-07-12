/**
 * This upload functionality assumes the following:
 */

macademia.upload = {};

macademia.upload.complete = function(event, queueId, fileObj, response, data) {
    var json = JSON.parse(response);
    $(".editPicture img").attr("src", json['path']);
    $(".editPicture input[name='largeImg']").attr('value', json['large']);
    $(".editPicture input[name='mediumImg']").attr('value', json['medium']);
    $(".editPicture input[name='smallImg']").attr('value', json['small']);
    return true;
};

macademia.upload.deleteImg = function() {
    var img = $(".editPicture img");
    img.attr("src", img.attr('defaultImage'));
    $(".editPicture input[name='largeImg']").attr('value', -1);
    $(".editPicture input[name='mediumImg']").attr('value', -1);
    $(".editPicture input[name='smallImg']").attr('value', -1);
    return false;
};

macademia.upload.init = function() {
  $('#imgUploader').uploadify({
      uploader : 'uploadify/uploadify.swf',
      script : '/Macademia/image/upload',
      folder : 'foo',
      auto : true,
      hideButton : true,
      wmode : 'transparent',
      multi : false,
      onComplete : macademia.upload.complete,
      cancelImg : '/Macademia/uploadify/cancel.png'
  });
  $(".editPicture .delete").click(pw.upload.deleteImg);
};

$().ready(macademia.upload.init);