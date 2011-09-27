Ext.define( 'Common.WizardLayout', {
    extend: 'Ext.layout.container.Card',
    alias: 'layout.wizard',
    mixins: ['Ext.util.Animate'],

    deferredRender : false,
    animate: true,
    renderHidden : false,
    easing: 'easeOut',
    duration: 1000,

    setActiveItem : function(item){

        var owner = this.owner;
        var newCard = this.parseActiveItem(item);
        var newIndex = owner.items.indexOf(newCard);

        if(this.activeItem != newCard){
            if (newCard.rendered && this.animate) {

                this.syncFx();

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