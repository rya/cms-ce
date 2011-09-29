Ext.define( 'Common.WizardLayout', {
    extend: 'Ext.layout.container.Card',
    alias: 'layout.wizard',
    mixins: ['Ext.util.Animate'],

    deferredRender : false,
    renderHidden : false,
    animation: 'slide',
    easing: 'easeOut',
    duration: 1000,

    setActiveItem : function(item){

        var owner = this.owner;
        var oldIndex = owner.items.indexOf( this.activeItem );
        var newCard = this.parseActiveItem(item);
        var newIndex = owner.items.indexOf(newCard);

        if( this.activeItem != newCard ){
            if( newCard.rendered && this.animation ) {

                this.syncFx();

                switch( this.animation ) {
                    case 'fade':
                        newCard.el.setStyle({
                            position: 'absolute',
                            opacity: 0,
                            top: this.getRenderTarget().getPadding('t') + 'px'
                        });
                        newCard.show();

                        if (this.activeItem) {
                            this.activeItem.el.fadeOut({
                                useDisplay: true,
                                duration: this.duration,
                                callback: function() {
                                    this.hide()
                                },
                                scope: this.activeItem
                            });
                        }
                        owner.doLayout();
                        newCard.el.fadeIn({
                            useDisplay: true,
                            duration: this.duration,
                            callback: function() {
                                newCard.el.setStyle({
                                    position: ''
                                });
                            },
                            scope: this
                        });
                    break;
                    case 'slide':
                        newCard.el.setStyle({
                            position: 'absolute',
                            visibility: 'hidden',
                            width: this.getRenderTarget().getWidth(),
                            top: this.getRenderTarget().getPadding('t') + 'px'
                        });
                        newCard.show();

                        if (this.activeItem) {
                            this.activeItem.el.slideOut(
                                newIndex > oldIndex ? "l" : "r",
                                {
                                    duration: this.duration,
                                    easing: this.easing,
                                    remove: false,
                                    scope: this.activeItem,
                                    callback: function() {
                                        this.hide()
                                    }
                            });
                        }

                        owner.doLayout();
                        newCard.el.slideIn(
                            newIndex > oldIndex ? "r" : "l",
                            {
                                duration: this.duration,
                                easing: this.easing,
                                scope: this,
                                callback: function() {
                                    newCard.el.setStyle({
                                        position: ''
                                    });
                                }
                        });
                    break;
                }

                this.activeItem = newCard;
                this.sequenceFx();

            } else {
                if(this.activeItem){
                    this.activeItem.hide();
                }
                this.activeItem = newCard;
                newCard.show();
                this.owner.doLayout();
            }
            return newCard;
        }
    }

});