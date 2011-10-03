Ext.define( 'Common.WizardLayout', {
    extend: 'Ext.layout.container.Card',
    alias: 'layout.wizard',
    mixins: ['Ext.util.Animate'],

    deferredRender : false,
    renderHidden : false,
    animation: 'slide',
    easing: 'easeOut',
    duration: 500,

    setActiveItem : function(item){

        var owner = this.owner;
        var oldCard = this.activeItem;
        var oldIndex = owner.items.indexOf( oldCard );
        var newCard = this.parseActiveItem(item);
        var newIndex = owner.items.indexOf(newCard);

        if( oldCard != newCard ){
            if( newCard.rendered && this.animation ) {

                this.syncFx();
                owner.fireEvent( "animationstarted", newCard, oldCard );

                switch( this.animation ) {
                    case 'fade':
                        newCard.el.setStyle({
                            position: 'absolute',
                            opacity: 0,
                            top: this.getRenderTarget().getPadding('t') + 'px'
                        });
                        newCard.show();

                        if (oldCard) {
                            oldCard.el.fadeOut({
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
                                if( Ext.isFunction( owner.onAnimationFinished ) )
                                    owner.onAnimationFinished( newCard, oldCard )
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

                        if (oldCard) {
                            oldCard.el.slideOut(
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
                                    if( Ext.isFunction( owner.onAnimationFinished ) )
                                        owner.onAnimationFinished( newCard, oldCard );
                                }
                        });
                    break;
                }

                this.activeItem = newCard;
                this.sequenceFx();

            } else {
                if(oldCard){
                    oldCard.hide();
                }
                this.activeItem = newCard;
                newCard.show();
                this.owner.doLayout();
            }
            return newCard;
        }
    }

});