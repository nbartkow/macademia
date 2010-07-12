    <div class="editPicture">
      <div>
        <img src="${image.retrieveWebPath()}"
             alt=""
             defaultImage="${defaultImage.retrieveWebPath()}"/>
      </div>
      <div class="links">
        <a href="#" class="change">change picture</a> | <a href="#" class="delete">delete</a>
        <div id="imgUploader">&nbsp;</div>
      </div>
      <input type="hidden" name="imgId" value="0"/>
    </div>