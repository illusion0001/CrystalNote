package com.xephorium.crystalnote.ui.about


class AboutPresenter : AboutContract.Presenter() {


    /*--- Lifecycle Methods ---*/


    /*--- Action Handling Methods ---*/

    override fun handleMenuButtonClick() {
        view?.showNavigationDrawer()
    }


    /*--- Private Methods ---*/

}
