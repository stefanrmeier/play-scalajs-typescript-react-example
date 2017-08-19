import * as React from 'react';

interface Props {
    visible: boolean;
}

class Spinner extends React.Component<Props, any> {
    constructor(props: any) {
        super(props);
    }

    render() {
        return (
            <div style={{visibility: (this.props.visible ? 'visible' : 'hidden')}}>
            <div style={{ display: 'block', background: 'rgba(51,51,51,0.4)', filter: 'progid:DXImageTransform.Microsoft.gradient(startColorstr=#7f333333, endColorstr=#7f333333)', top: 0, width: '100%', height: '100%', zIndex: 999999, position: 'fixed' }}>
                <div className='uil-spin-css' style={{ left: '50%', marginLeft: '-84px', top: '50%', marginTop: '-84px', transform: 'translateY(-50%)', position: 'absolute', display: 'block', WebkitTransform: 'scale(0.78)' }}><div><div></div></div><div><div></div></div><div><div></div></div><div><div></div></div><div><div></div></div><div><div></div></div><div><div></div></div><div><div></div></div></div>
            </div>
            </div>
        );
    }
}

export default Spinner;