Ext.define( 'Common.WizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.wizardPanel',
    requires: ['Common.WizardLayout'],
    layout: 'wizard',
    cls: 'cms-wizard',
    autoScroll: true,
    defaults: {
        border: false,
        padding: 10
    },
    externalControls: undefined,
    showControls: true,
    data: {},

    initComponent: function()
    {
        var wizard = this;

        if ( this.showControls )
        {
            this.bbar = {
                xtype: 'toolbar',
                itemId: 'controls',
                items: [
                    {
                        text: 'Prev',
                        itemId: 'prev',
                        disabled: true,
                        handler: function( btn, evt )
                        {
                            wizard.prev();
                        }
                    },
                    {
                        text: 'Next',
                        itemId: 'next',
                        handler: function( btn, evt )
                        {
                            wizard.next();
                        }
                    }
                ]
            }
        }

        this.dockedItems = [{
            xtype: 'panel',
            dock: 'top',
            listeners: {
                click: {
                    fn: wizard.changeStep,
                    element: 'body',
                    scope: wizard
                }
            },
            styleHtmlContent: true,
            margin: 0,
            tpl: new Ext.XTemplate( Templates.common.wizardPanelSteps, {
                isCurrent: function( index ) {
                    return wizard.getLayout().getActiveItem()
                            == wizard.getComponent( index );
                }
            })
        }];

        this.callParent( arguments );
        this.addEvents( "beforestepchanged", "stepchanged", "animationstarted", "animationfinished", "finished" );
        this.on( "animationstarted", this.onAnimationStarted );
        this.on( "animationfinished", this.onAnimationFinished );
        this.updateProgress();
    },

    changeStep: function(event, target){
        var element = Ext.fly(target);
        if (element.hasCls('text')){
            element = element.up('a');
        }
        if (element.hasCls('step')){
            var step = Number(element.getAttribute('wizardStep'));
            this.navigate(step - 1);
        }
    },

    next: function( btn )
    {
        return this.navigate( "next", btn );
    },

    prev: function( btn )
    {
        return this.navigate( "prev", btn );
    },

    getNext: function()
    {
        return this.getLayout().getNext();
    },

    getPrev: function()
    {
        return this.getLayout().getPrev();
    },

    navigate: function( direction, btn )
    {
        var oldStep = this.getLayout().getActiveItem();
        if ( this.fireEvent( "beforestepchanged", this, oldStep ) != false )
        {
            var newStep;
            switch ( direction ) {
                case "-1":
                case "prev":
                    if ( this.getPrev() ) {
                        newStep = this.getLayout().prev();
                    }
                    break;
                case "+1":
                case "next":
                    if ( this.getNext() ) {
                        newStep = this.getLayout().next();
                    } else {
                        this.fireEvent( "finished", this, this.getData()  );
                    }
                    break;
                default:
                    newStep = this.getLayout().setActiveItem( direction );
                    break;
            }
            if( newStep ) {
                this.updateProgress();
                this.externalControls = btn.up( 'toolbar' );
                if ( this.showControls ) {
                    // disable internal controls if shown
                    this.updateButtons( this.getDockedComponent('controls'), true );
                }
                if ( this.externalControls ) {
                    // try to disable external controls
                    this.updateButtons( this.externalControls, true );
                }
            }
        }
    },

    onAnimationStarted: function( newStep, oldStep ) {

    },

    onAnimationFinished: function( newStep, oldStep ) {
        if ( newStep )
        {
            this.fireEvent( "stepchanged", this, oldStep, newStep );
            if ( this.showControls ) {
                // update internal controls if shown
                this.updateButtons( this.getDockedComponent('controls') );
            }
            if ( this.externalControls ) {
                // try to update external controls
                this.updateButtons( this.externalControls );
            }
            return newStep;
        }
    },

    updateProgress: function() {
        this.dockedItems.items[0].update( this.items.items );
    },

    updateButtons: function( toolbar, disable )
    {
        if( toolbar ) {
            var prev = toolbar.down( '#prev' ),
                next = toolbar.down( '#next' ),
                finish = toolbar.down( '#finish' ),
                save = toolbar.down( '#save' );
            var hasNext = this.getNext(),
                hasPrev = this.getPrev();
            if( prev ) {
                prev.setDisabled( disable || !hasPrev );
            }
            if( next ) {
                if ( finish ) {
                    next.setDisabled( disable || !hasNext );
                    finish.setDisabled( disable || hasNext )
                } else {
                    next.setText( hasNext ? 'Next' : 'Finish' );
                    next.setDisabled( disable );
                }
            }
            if ( save ) {
                save.setDisabled( disable || !hasNext );
            }
        }
    },

    addData: function( newValues )
    {
        Ext.apply( this.data, newValues );
    },

    getData: function()
    {
        return this.data;
    }

} );
