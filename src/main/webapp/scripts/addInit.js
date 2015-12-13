function addOnload(){
    $('#add-section').html(addPage.addForm({}).content);
    var $container = $('#add-container');
    setSubmitEvent($container);

    var $el = $container.find('a');
    var $img = $container.find('img');
    var $input = $container.find("[name='captcha']");
    Captcha.init($el, $img);
    Captcha.link($el, $input);

    submitOnEnter("add-container");
}

function postInit(){
    var $container = $('#add-container');
    setSubmitEvent($container);
    Captcha.init($container.find('a'), $container.find('img'));
    submitOnEnter("add-container");
}

function submitOnEnter(id){
    $('#' + id).find('input').keyup(function(event){
        if(event.keyCode == 13) {
            $('#' + id).find('button').click();
        }
    });
}

/**
 * $container, The container containing the data generated by addPage.addForm.
 */
function setSubmitEvent($container){
    $container.find('button').on('click', function(){
        $(this).prop('disabled', true);
        Dialog.loading({
            id: 'loading-section',
            text: 'Adding URL, please wait...',
            opacity: 0.2
        });

        Ajax.POST({
            url: Constants.Rest.ADD,
            data:{
                urlName: $container.find("[name='url']").val(),
                link: $container.find("[name='link']").val(),
                groupName: $container.find("[name='group']").val(),
                password: $container.find("[name='password']").val(),
                captcha: $container.find("[name='captcha']").val()
            },
            success:function(data, textStatus, jqXHR){
                postAdd(data, true);
            },
            error:function(jqXHR, textStatus, errorThrown){
                postAdd($.parseJSON(jqXHR.responseText), false);
            }
        });
    });
}

function postAdd(response, success){
    $('#loading-section').remove();

    $('#add-section').html(addPage.addForm({
        urlName: response.urlName,
        urlError: response.urlError,
        link: response.link,
        linkError: response.linkError,
        groupName: response.groupName,
        groupError: response.groupError,
        password: response.password,
        passwordError: response.passwordError,
        captcha: response.captcha,
        captchaError: response.captchaError
    }).content);

    postInit();

    if(success){
        if(!$('#info-table').length){
            $('#info-section').html(addPage.infoHeader({}).content);
        }

        $('#info-table').append(addPage.infoRow({
            url: Constants.DOMAIN + Constants.REST + (Objects.typed(response.groupName) ? "/" + response.groupName : "") + "/" + response.urlName,
            link: (response.link.indexOf('http') == 0 ? response.link : "http://" + response.link),
            group: response.group
        }).content);

        Dialog.popup({
            text: "The link was successfully added!",
            img: "img/success.gif",
            life: 3000
        });
    } else {
        Dialog.popup({
            text: "Failed to add the link.",
            img: "img/fail.png",
            life: 3000
        });
    }
}
