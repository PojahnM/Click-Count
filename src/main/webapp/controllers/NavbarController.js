function NavbarController($scope) {
    $scope.navbarItems = [
        {
            url: 'templates/home.html',
            icon:  'home',
            label: 'Home'
        },
        {
            url: 'templates/add.html',
            icon:  'add',
            label: 'Add'
        },
        {
            url: 'templates/view.html',
            icon:  'remove_red_eye',
            label: 'View'
        },
        {
            url: 'templates/terms.html',
            icon:  'assignment',
            label: 'Terms of Use'
        },
        {
            url: 'templates/contact.html',
            icon:  'email',
            label: 'Contact'
        }
    ];
}

angular.module('ClickCount').controller('NavbarController', [
    '$scope',
    NavbarController]);